use std::fmt::{Display, Formatter};

pub enum Side {
    Top,
    Bottom,
    Left,
    Right,
    Front,
    Back,
}

impl Display for Side {
    fn fmt(&self, f: &mut Formatter<'_>) -> std::fmt::Result {
        let out = match self {
            Side::Top => "top",
            Side::Bottom => "bottom",
            Side::Left => "left",
            Side::Right => "right",
            Side::Front => "front",
            Side::Back => "back",
        };

        write!(f, "{out}")
    }
}
