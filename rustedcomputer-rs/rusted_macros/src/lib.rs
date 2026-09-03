use proc_macro::TokenStream;
use quote::quote;
use syn::{ItemFn, parse_macro_input};

#[proc_macro_attribute]
pub fn async_main_fn(_attr: TokenStream, item: TokenStream) -> TokenStream {
    let input_fn = parse_macro_input!(item as ItemFn);

    let ItemFn {
        attrs,
        vis,
        sig,
        block,
        ..
    } = input_fn;

    if sig.asyncness.is_none() {
        return syn::Error::new_spanned(
            sig.fn_token,
            "#[rusted_computer::main] can only be applied to `async fn`",
        )
        .to_compile_error()
        .into();
    }

    let mut new_sig = sig.clone();
    new_sig.asyncness = None;

    let expanded = quote! {
        #(#attrs)*
        #vis #new_sig {
            rustedcomputer::async_runtime::block_on(async #block)
        }
    };

    expanded.into()
}
