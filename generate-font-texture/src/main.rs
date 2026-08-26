#![allow(unused)]

use std::{collections::HashMap, error::Error, fs};

use image::{ImageBuffer, Rgb, RgbImage, Rgba, RgbaImage};
use serde::Deserialize;

#[derive(Debug, Deserialize)]
struct Characters(Vec<Character>);

#[derive(Debug, Deserialize)]
struct Character {
    character: String,
    name: String,
    codepoint: u32,
    group: Option<String>,

    /// The bitmap of this char
    pixels: Option<Vec<Vec<u8>>>,

    /// The number of pixels this char goes below the base line
    descent: Option<u8>,

    /// Reference to char when this is a diacritic
    reference: Option<u32>,

    diacritic: Option<String>,
    /// Space between char diacritic above
    #[serde(rename = "diacriticSpace")]
    diacritic_space: Option<u8>,

    /// idk what this is, just ignore it
    #[serde(rename = "leftMargin")]
    left_margin: Option<f32>,
}

#[derive(Debug, Deserialize)]
struct Diacritics(HashMap<String, Diacritic>);

#[derive(Debug, Deserialize)]
struct Diacritic {
    pixels: Vec<Vec<u8>>,
}

const TEXTURE_WIDTH_CHARS: u32 = 20;
const DIACRITIC_HEIGHT: u32 = 3;
const CHAR_WIDTH: u32 = 5;
const CHAR_HEIGHT: u32 = 9 + DIACRITIC_HEIGHT;

fn main() -> Result<(), Box<dyn Error>> {
    let diacritics_file = fs::read_to_string("font-src/diacritics.json")?;
    let diacritics: Diacritics = serde_json::from_str(&diacritics_file)?;
    let diacritics = diacritics.0;

    let characters_file = fs::read_to_string("font-src/characters.json")?;
    let characters: Characters = serde_json::from_str(&characters_file)?;
    let characters = characters.0;

    let my_a = characters
        .iter()
        .find(|character| character.character == "a")
        .unwrap();

    let image_width = CHAR_WIDTH * TEXTURE_WIDTH_CHARS * CHAR_WIDTH;
    let image_height = CHAR_HEIGHT
        * (characters.len() as f32 / (TEXTURE_WIDTH_CHARS * CHAR_WIDTH) as f32).ceil() as u32;

    let mut image = RgbaImage::new(image_width, image_height);

    let mut current_x = 0;
    let mut current_y = 0;
    let mut wide_counter = 0;

    print!(r#"""""#);

    'character_loop: for character in characters.iter() {
        if character.character.chars().count() > 1 {
            continue 'character_loop;
        }

        if let Some(pixels) = &character.pixels {
            if (pixels[0].len() as u32 > CHAR_WIDTH
                || (character.character != " " && all_are_null(pixels)))
            {
                continue 'character_loop;
            }

            write_bitmap(&mut image, pixels, DIACRITIC_HEIGHT, &current_x, &current_y);
        } else if let Some(diacritic) = &character.diacritic {
            let diacritic_space = character.diacritic_space.unwrap() as u32;

            let reference = character.reference.unwrap();
            let referenced_character = characters
                .iter()
                .find(|character| character.codepoint == reference)
                .unwrap();

            let mut pixels = referenced_character.pixels.clone().unwrap();

            let mut empty_rows = 0;

            for row in &pixels {
                if row.contains(&1) {
                    break;
                } else {
                    empty_rows += 1;
                }
            }

            let diacritic = &diacritics.get(diacritic).as_ref().unwrap().pixels;
            let diacritic = [
                vec![vec![0; CHAR_WIDTH as usize]; empty_rows],
                diacritic.clone(),
            ]
            .concat();

            write_bitmap(
                &mut image,
                &diacritic,
                ((DIACRITIC_HEIGHT + empty_rows as u32) - diacritic.len() as u32 - diacritic_space),
                &current_x,
                &current_y,
            );

            for _ in 0..empty_rows {
                pixels.remove(0);
            }

            write_bitmap(
                &mut image,
                &pixels,
                DIACRITIC_HEIGHT + empty_rows as u32,
                &current_x,
                &current_y,
            );
        } else {
            let reference = character.reference.unwrap();
            let referenced_character = characters
                .iter()
                .find(|character| character.codepoint == reference)
                .unwrap();

            write_bitmap(
                &mut image,
                referenced_character.pixels.as_ref().unwrap(),
                DIACRITIC_HEIGHT,
                &current_x,
                &current_y,
            );
        }

        print!("{}", character.character);

        current_x += CHAR_WIDTH;

        if (current_x >= image_width - 1) {
            current_x = 0;
            current_y += CHAR_HEIGHT;
            // println!();
        }
    }
    println!(r#"""""#);

    image.save("out/terminal_font.png")?;

    Ok(())
}

fn write_bitmap(
    image: &mut RgbaImage,
    bitmap: &[Vec<u8>],
    y_offset: u32,
    current_x: &u32,
    current_y: &u32,
) {
    for (y, row) in bitmap.iter().enumerate() {
        for (x, pixel) in row.iter().enumerate() {
            let y = y + y_offset as usize;

            let image_pixel = image.get_pixel_mut(current_x + x as u32, current_y + y as u32);

            if *pixel == 1 {
                *image_pixel = Rgba([255; 4]);
            } else {
                *image_pixel = Rgba([0; 4]);
            }
        }
    }
}

fn all_are_null(bitmap: &[Vec<u8>]) -> bool {
    for row in bitmap {
        for pixel in row {
            if (*pixel == 1) {
                return false;
            }
        }
    }

    true
}
