use std::ops::{Deref, DerefMut};

pub struct FutureId(i32);

impl From<i32> for FutureId {
    fn from(value: i32) -> Self {
        Self(value)
    }
}

impl Deref for FutureId {
    type Target = i32;

    fn deref(&self) -> &Self::Target {
        &self.0
    }
}

impl DerefMut for FutureId {
    fn deref_mut(&mut self) -> &mut Self::Target {
        &mut self.0
    }
}