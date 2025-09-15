pub struct PtrLen<T> {
    pub ptr: *const T,
    pub len: usize,
}

impl<T> PtrLen<T> {
    pub fn new(ptr: *const T, len: usize) -> Self {
        Self {
            ptr,
            len,
        }
    }

    /// Returns the pointer as an `i32` to pass it to WASM.
    ///
    /// This cast works, because pointers in WASM are never longer than 32 bits.
    pub fn as_wasm_ptr(&self) -> i32 {
        self.ptr as i32
    }

    /// Returns the length as an `i32` to pass it to WASM.
    ///
    /// A string probably shouldn't be longer than 32 bits.
    pub fn as_wasm_len(&self) -> i32 {
        self.len as i32
    }
}

impl<T> From<i64> for PtrLen<T> {
    fn from(value: i64) -> Self {
        let length = (value & 0xFFFFFFFF) as usize;
        let pointer = (value >> 32) as *mut T;

        PtrLen::new(pointer, length)
    }
}

pub trait GetPtrLen {
    type PointerType;

    /// Gets the pointer and length to this type and combines it in a struct.
    fn get_ptr_len(&self) -> PtrLen<Self::PointerType>;
}

impl GetPtrLen for String {
    type PointerType = u8;

    fn get_ptr_len(&self) -> PtrLen<Self::PointerType> {
        PtrLen::new(self.as_ptr(), self.len())
    }
}

impl<T> GetPtrLen for Vec<T> {
    type PointerType = T;

    fn get_ptr_len(&self) -> PtrLen<Self::PointerType> {
        PtrLen::new(self.as_ptr(), self.len() * size_of::<T>())
    }
}