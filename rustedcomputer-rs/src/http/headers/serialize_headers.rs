use http::HeaderMap;

/// Collects the addresses and lengths of the header names and values and puts them into a [`Vec`].
///
/// This is needed for interfacing with Java.
///
/// The caller has to make sure that the [`HeaderMap`] isn't deallocated when the return value of
/// this function is used.
pub fn serialize_headers(headers: &HeaderMap) -> Vec<usize> {
    let mut serialized = Vec::new();

    for (header_name, header_value) in headers {
        let name_length = header_name.as_str().len();
        let name_pointer = header_name.as_str().as_ptr();

        let value_length = header_value.as_bytes().len();
        let value_pointer = header_value.as_bytes().as_ptr();

        serialized.push(name_pointer as usize);
        serialized.push(name_length);
        serialized.push(value_pointer as usize);
        serialized.push(value_length);
    }

    serialized
}
