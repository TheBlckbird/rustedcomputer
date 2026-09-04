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
    F: Fn(Option<&[u8]>) -> T,
{
    pub id: FutureId,
    pub converter: F,
}

impl<T, F> RustedFuture<T, F>
where
    F: Fn(Option<&[u8]>) -> T,
{
    pub fn new<G>(caller: G, converter: F) -> Self
    where
        G: Fn() -> FutureId,
    {
        let id = caller();
        Self { id, converter }
    }
}

impl RustedFuture<(), fn(Option<&[u8]>)> {
    pub fn without_converter<G>(caller: G) -> Self
    where
        G: Fn() -> FutureId,
    {
        fn converter(_: Option<&[u8]>) {}
        let id = caller();
        Self { id, converter }
    }
}

impl<T, F> Future for RustedFuture<T, F>
where
    F: Fn(Option<&[u8]>) -> T,
{
    type Output = Result<T>;

    fn poll(self: Pin<&mut Self>, _cx: &mut Context<'_>) -> Poll<Self::Output> {
        let poll_result = unsafe { extern_fns::poll(*self.id) };

        // Special cases where this is not a pointer
        if poll_result == -1 {
            return Poll::Ready(Err(RustedError::UnknownFutureId));
        } else if poll_result == 0 {
            let converted = (self.converter)(None);
            return Poll::Ready(Ok(converted));
        }

        let length = (poll_result & 0xFFFFFFFF) as usize;
        let pointer = (poll_result >> 32) as *mut u8;

        // SAFETY: pointer and length transmitted from Java should be safe if it isn't null or -1
        let slice = unsafe { slice::from_raw_parts_mut(pointer, length) };
        let future = unsafe { Box::from_raw(slice) };
        let (status, result) = future.split_first().unwrap_or((&future[0], &[]));

        match status {
            0 => Poll::Pending,
            1 => Poll::Ready(Err(RustedError::FutureFailed)),
            2 => {
                let converted = (self.converter)(Some(result));
                Poll::Ready(Ok(converted))
            }
            _ => Poll::Ready(Err(RustedError::FutureUnexpectedStatus(*status))),
        }
    }
}

mod extern_fns {
    #[link(wasm_import_module = "future")]
    unsafe extern "C" {
        pub unsafe fn poll(future_id: i32) -> i64;
    }
}
