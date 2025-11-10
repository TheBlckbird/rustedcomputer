use std::collections::HashMap;
use std::slice;

/// Retrieves the [`HeaderMap`] from the addresses and lengths returned by Java.
pub fn deserialize_headers(raw_headers: &[u8]) -> HashMap<String, String> {
    let mut headers = HashMap::new();

    let mut next_ptr = None;
    let mut next_header_name: Option<String> = None;

    for single_ptr_or_len in raw_headers.chunks_exact(4) {
        let ptr_or_len = u32::from_be_bytes(single_ptr_or_len.try_into().unwrap());

        if next_ptr.is_none() {
            next_ptr = Some(ptr_or_len as *mut u8);
        } else {
            let slice =
                unsafe { slice::from_raw_parts_mut(next_ptr.unwrap(), ptr_or_len as usize) };
            let boxed_slice = unsafe { Box::from_raw(slice) };
            let header_name_or_value = str::from_utf8(&boxed_slice).unwrap().to_owned();

            if next_header_name.is_none() {
                next_header_name = Some(header_name_or_value);
            } else {
                headers.insert(next_header_name.unwrap(), header_name_or_value);
                next_header_name = None;
            }

            next_ptr = None;
        }
    }

    headers
}
