use http::Method;

macro_rules! match_method {
    {match ($method:expr) {$($name:ident => $return_expr:expr),*$(,)?}} => {
        $(if $method == http::Method::$name {
            return $return_expr;
        })*

        return -1
    };
}

pub fn method_to_int(method: &Method) -> i32 {
    match_method! {
          match (method) {
              GET => 0,
              HEAD => 1,
              POST => 2,
              PUT => 3,
              DELETE => 4,
              CONNECT => 5,
              OPTIONS => 6,
              TRACE => 7,
              PATCH => 8,
          }
    }
}
