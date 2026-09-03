use crate::async_runtime::future::RustedFuture;
use crate::error::Result;
use crate::side::Side;

pub async fn set_output(side: Side, power: u8) -> Result<()> {
    let side = side.to_string();
    let side_length = side.len() as i32;
    let side_offset = side.as_ptr() as i32;

    let caller =
        || unsafe { extern_fns::set_output(side_length, side_offset, power as i32) }.into();

    RustedFuture::without_converter(caller).await
}

pub async fn get_input(side: Side) -> Result<u8> {
    let side = side.to_string();
    let side_length = side.len() as i32;
    let side_offset = side.as_ptr() as i32;

    let caller = || unsafe { extern_fns::get_input(side_length, side_offset) }.into();
    let converter = |value: &[u8]| value[0];

    RustedFuture::new(caller, converter).await
}

mod extern_fns {
    #[link(wasm_import_module = "redstone")]
    unsafe extern "C" {
        pub unsafe fn set_output(side_length: i32, side_offset: i32, power: i32) -> i32;
        pub unsafe fn get_input(side_length: i32, side_offset: i32) -> i32;
    }
}
