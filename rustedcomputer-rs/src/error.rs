use thiserror::Error;

#[derive(Error, Debug)]
pub enum RustedError {
    #[error("Future failed to resolve")]
    FutureFailed,
    #[error("Future had a timeout")]
    FutureTimeout,
    #[error("HTTP Error: {0}")]
    HttpError(http::Error),
    #[error("HTTP Connection Issue")]
    Connect,
    #[error("Java returned an IOException")]
    IO,
    #[error("Java returned an InterruptedException")]
    Interrupted,
    #[error("Java returned a SecurityException")]
    Security,
}

impl From<http::Error> for RustedError {
    fn from(value: http::Error) -> Self {
        Self::HttpError(value)
    }
}

pub type Result<T, E = RustedError> = std::result::Result<T, E>;
