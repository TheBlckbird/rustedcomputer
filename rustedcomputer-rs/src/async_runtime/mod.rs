use std::pin::Pin;
use std::{ptr, slice, thread};
use std::task::{Context, Poll, RawWaker, RawWakerVTable, Waker};
use std::time::Duration;
use crate::async_runtime::future_id::FutureId;
use crate::error::{Result, RustedComputerError};

pub mod future_id;

pub struct RustedComputerFuture<T, F>
where
    F: Fn(String) -> T,
{
    pub id: FutureId,
    pub converter: F,
}

impl<T, F> RustedComputerFuture<T, F>
where
    F: Fn(String) -> T,
{
    pub fn new<G>(caller: G, converter: F) -> Self
    where
        G: Fn() -> FutureId,
    {
        let id = caller();
        Self { id, converter }
    }
}

impl<T, F> Future for RustedComputerFuture<T, F>
where
    F: Fn(String) -> T,
{
    type Output = Result<T>;

    fn poll(self: Pin<&mut Self>, _cx: &mut Context<'_>) -> Poll<Self::Output> {
        let poll_result = unsafe { poll(*self.id) };

        let length = (poll_result & 0xFFFFFFFF) as usize;
        let pointer = (poll_result >> 32) as *mut u8;

        let slice = unsafe { slice::from_raw_parts_mut(pointer, length) };
        let boxed_slice = unsafe { Box::from_raw(slice) };
        let future = str::from_utf8(&boxed_slice).unwrap();

        if future == "P" {
            Poll::Pending
        } else if future == "F" {
            Poll::Ready(Err(RustedComputerError::FutureFailed))
        } else if future.starts_with("S") {
            let future_result = future[1..].to_owned();
            let converted_result = (self.converter)(future_result);

            Poll::Ready(Ok(converted_result))
        } else {
            unreachable!()
        }
    }
}

fn noop_raw_waker() -> RawWaker {
    fn clone(_: *const ()) -> RawWaker {
        noop_raw_waker()
    }
    fn no_op(_: *const ()) {}

    static VTABLE: RawWakerVTable = RawWakerVTable::new(clone, no_op, no_op, no_op);
    RawWaker::new(ptr::null(), &VTABLE)
}

fn noop_waker() -> Waker {
    unsafe { Waker::from_raw(noop_raw_waker()) }
}

pub fn block_on<F: Future>(mut future: F) -> F::Output {
    let mut future = unsafe { Pin::new_unchecked(&mut future) };
    let waker = noop_waker();
    let mut cx = Context::from_waker(&waker);

    loop {
        match future.as_mut().poll(&mut cx) {
            Poll::Ready(val) => return val,
            Poll::Pending => {
                thread::sleep(Duration::from_millis(100));
            }
        }
    }
}

#[link(wasm_import_module = "future")]
unsafe extern "C" {
    #[link_name = "poll"]
    pub fn poll(future_id: i32) -> i64;
}
