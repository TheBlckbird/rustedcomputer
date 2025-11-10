use crate::http::deserialize_response::deserialize_response;
use crate::wasm_helpers::PtrLen;
use crate::{http::method_to_int::method_to_int, wasm_helpers::GetPtrLen};
use headers::serialize_headers::serialize_headers;
use std::slice;

pub use http::*;

mod deserialize_response;
mod headers;
mod method_to_int;

pub fn fetch(request: Request<String>) -> crate::Result<Response<String>> {
    let extension = request.extensions();
    if !extension.is_empty() {
        panic!("Request extensions aren't supported!");
    }

    let method = method_to_int(request.method());

    // A temporary variable has to be created in order to retrieve a valid pointer.
    let uri = request.uri().to_string();
    let uri_ptr_len = uri.get_ptr_len();

    let headers_serialized = serialize_headers(request.headers());
    let headers_ptr_len = headers_serialized.get_ptr_len();
    let body_ptr_len = request.body().get_ptr_len();

    println!(
        "{}; {}",
        headers_ptr_len.as_wasm_ptr(),
        headers_ptr_len.as_wasm_len()
    );

    let raw_response = unsafe {
        let raw_response_ptr_len: PtrLen<u8> = PtrLen::from(extern_fns::fetch(
            method,
            uri_ptr_len.as_wasm_ptr(),
            uri_ptr_len.as_wasm_len(),
            body_ptr_len.as_wasm_ptr(),
            body_ptr_len.as_wasm_len(),
            headers_ptr_len.as_wasm_ptr(),
            headers_ptr_len.as_wasm_len(),
        ));

        slice::from_raw_parts(raw_response_ptr_len.ptr, raw_response_ptr_len.len)
    };

    deserialize_response(raw_response)
}

mod extern_fns {
    #[link(wasm_import_module = "http")]
    unsafe extern "C" {
        #[link_name = "fetch"]
        pub fn fetch(
            method: i32,
            uri_address: i32,
            uri_length: i32,
            body_address: i32,
            body_length: i32,
            headers_address: i32,
            headers_length: i32,
        ) -> i64;
    }
}
