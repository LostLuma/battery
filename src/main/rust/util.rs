use jni::{
    objects::{JObject, JString, JValueGen},
    sys::jlong,
    JNIEnv,
};

use crate::Result;

pub fn as_descriptor(class: &str) -> String {
    format!("L{class};")
}

pub fn into_box<T>(t: T) -> jlong {
    Box::into_raw(Box::from(t)) as jlong
}

pub fn drop_box<T>(ptr: jlong) {
    #[allow(unused_variables)]
    let value = unsafe { Box::from_raw(ptr as *mut T) };
}

fn get_pointer(env: &mut JNIEnv, object: &JObject) -> Result<jlong> {
    Ok(env.get_field(object, "ptr", "J")?.j()?)
}

pub fn pull_box<'r, T>(env: &mut JNIEnv, object: &JObject) -> Result<&'r mut T> {
    let pointer = get_pointer(env, object)?;
    Ok(unsafe { &mut *(pointer as *mut T) })
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
