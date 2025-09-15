use crate::http::headers::deserialize_headers::deserialize_headers;
use http::Response;
use std::slice;

pub fn deserialize_response(raw_response: &[u8]) -> http::Result<Response<String>> {
    let mut raw_response = raw_response.iter();

    let status_code =
        ((*raw_response.next().unwrap() as u16) << 8) | *raw_response.next().unwrap() as u16;

    let http_version = match raw_response.next().unwrap() {
        11 => http::Version::HTTP_11,
        2 => http::Version::HTTP_2,
        _ => unreachable!(),
    };

    let headers_ptr = take_next_u32(&mut raw_response) as *const u8;
    let headers_len = take_next_u32(&mut raw_response) as usize;
    let raw_headers = unsafe { slice::from_raw_parts(headers_ptr, headers_len) };
    let headers = deserialize_headers(raw_headers);

    let body = str::from_utf8(raw_response.copied().collect::<Vec<u8>>().as_slice())
        .unwrap()
        .to_owned();

    let mut response_builder = Response::builder()
        .status(status_code)
        .version(http_version);

    for (name, value) in headers {
        // Pseudo headers are for some reason not accepted by the `http`-crate
        if !name.starts_with(':') {
            response_builder = response_builder.header(name, value);
        }
    }

    response_builder.body(body)
}

fn take_next_u32(raw_response: &mut slice::Iter<u8>) -> u32 {
    u32::from_be_bytes(
        raw_response
            .by_ref()
            .take(4)
            .copied()
            .collect::<Vec<u8>>()
            .try_into()
            .unwrap(),
    )
}
