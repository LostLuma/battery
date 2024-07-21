use jni::{
    objects::{JObject, JString, JValueGen},
    sys::jlong,
    JNIEnv,
};

use crate::Result;

pub fn as_descriptor(class: &str) -> String {
    format!("L{class};")
}

pub fn get_ptr<'a>(env: &mut JNIEnv<'a>, this: &JObject<'a>) -> Result<jlong> {
    Ok(env.get_field(this, "ptr", "J")?.try_into()?)
}

pub fn get_enum_member<'a>(
    env: &mut JNIEnv<'a>,
    class: &str,
    value: impl ToString,
) -> Result<JValueGen<JObject<'a>>> {
    let descriptor = as_descriptor(class);
    // Convert member name from e.g. "lithium-ion" to "LITHIUM_ION"
    let value = value.to_string().to_ascii_uppercase().replace("-", "_");

    let class = env.find_class(class)?;
    let field = env.get_static_field(class, value, &descriptor)?;

    Ok(field)
}

pub trait ToJString {
    fn to_jstring<'a>(self, env: &mut JNIEnv<'a>) -> Result<JString<'a>>;
}

impl ToJString for Option<&str> {
    fn to_jstring<'a>(self, env: &mut JNIEnv<'a>) -> Result<JString<'a>> {
        match self {
            Some(value) => Ok(env.new_string(value)?),
            None => Ok(JString::from(JObject::null())),
        }
    }
}
