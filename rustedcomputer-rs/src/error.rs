use thiserror::Error;

#[derive(Error, Debug)]
pub enum RustedComputerError {
    #[error("Future failed to resolve")]
    FutureFailed,
    #[error("Future had a timeout")]
    FutureTimeout,
}

pub type Result<T, E = RustedComputerError> = std::result::Result<T, E>;