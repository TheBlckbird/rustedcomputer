use crate::{
    async_runtime::future_id::FutureId,
    error::{Result, RustedError},
};

use std::{
    pin::Pin,
    slice,
    task::{Context, Poll},
};

pub struct RustedFuture<T, F>
where
    F: Fn(&[u8]) -> T,
{
    pub id: FutureId,
    pub converter: F,
}

impl<T, F> RustedFuture<T, F>
where
    F: Fn(&[u8]) -> T,
{
    pub fn new<G>(caller: G, converter: F) -> Self
    where
        G: Fn() -> FutureId,
    {
        let id = caller();
        Self { id, converter }
    }
}

impl RustedFuture<(), fn(&[u8])> {
    pub fn without_converter<G>(caller: G) -> Self
    where
        G: Fn() -> FutureId,
    {
        fn converter(_: &[u8]) {}
        let id = caller();
        Self { id, converter }
    }
}

impl<T, F> Future for RustedFuture<T, F>
where
    F: Fn(&[u8]) -> T,
{
    type Output = Result<T>;

    fn poll(self: Pin<&mut Self>, _cx: &mut Context<'_>) -> Poll<Self::Output> {
        let poll_result = unsafe { extern_fns::poll(*self.id) };

        let length = (poll_result & 0xFFFFFFFF) as usize;
        let pointer = (poll_result >> 32) as *mut u8;

        // SAFETY: pointer transmitted from Java should be safe
        let slice = unsafe { slice::from_raw_parts_mut(pointer, length) };
        let future = unsafe { Box::from_raw(slice) };
        let Some((status, result)) = future.split_first() else {
            panic!("Got empty future")
        };

        match status {
            0 => Poll::Pending,
            1 => Poll::Ready(Err(RustedError::FutureFailed)),
            2 => {
                let converted = (self.converter)(result);
                Poll::Ready(Ok(converted))
            }
            _ => panic!("Unexpected Future status {status}"),
        }
    }
}

mod extern_fns {
    #[link(wasm_import_module = "future")]
    unsafe extern "C" {
        pub unsafe fn poll(future_id: i32) -> i64;
    }
}
