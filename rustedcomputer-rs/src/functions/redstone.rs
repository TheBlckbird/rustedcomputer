use crate::async_runtime::RustedComputerFuture;
use crate::error::Result;
use crate::side::Side;

pub async fn set_output(side: Side, power: u8) -> Result<()> {
    let side = side.to_string();
    let side_length = side.len() as i32;
    let side_offset = side.as_ptr() as i32;

    let caller = || unsafe { ext_set_output(side_length, side_offset, power as i32) }.into();
    let converter = |_| ();

    RustedComputerFuture::new(caller, converter).await
}

pub async fn get_input(side: Side) -> Result<u8> {
    let side = side.to_string();
    let side_length = side.len() as i32;
    let side_offset = side.as_ptr() as i32;

    let caller = || unsafe { ext_get_input(side_length, side_offset) }.into();
    let converter = |value: String| value.parse().unwrap();

    RustedComputerFuture::new(caller, converter).await
}

#[link(wasm_import_module = "redstone")]
unsafe extern "C" {
    #[link_name = "set_output"]
    fn ext_set_output(side_length: i32, side_offset: i32, power: i32) -> i32;

    #[link_name = "get_input"]
    fn ext_get_input(side_length: i32, side_offset: i32) -> i32;
}
