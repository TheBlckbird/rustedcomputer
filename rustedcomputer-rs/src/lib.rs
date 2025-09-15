pub use crate::http::*;

pub mod async_runtime;
pub mod side;
pub mod functions;
pub mod error;
pub mod http;
mod wasm_helpers;

#[unsafe(no_mangle)]
pub extern "C" fn alloc(length: i32) -> i32 {
    let buffer = vec![0u8; length as usize];
    let boxed_slice = buffer.into_boxed_slice();
    Box::into_raw(boxed_slice) as *mut u8 as i32
}
