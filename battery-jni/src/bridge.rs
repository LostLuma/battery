use jni::{
    objects::{JClass, JObject, JObjectArray},
    sys::jlong,
    JNIEnv,
};

use crate::{
    create_manager, drop_battery, drop_manager, get_batteries, throw_io_exception, update_battery,
};

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_create<'a>(
    mut env: JNIEnv<'a>,
    _class: JClass<'a>,
) -> jlong {
    match create_manager() {
        Ok(value) => value,
        Err(error) => {
            throw_io_exception(&mut env, error);
            jlong::default()
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_batteries0<'a>(
    mut env: JNIEnv<'a>,
    this: JObject<'a>,
) -> JObjectArray<'a> {
    match get_batteries(&mut env, &this) {
        Ok(value) => value,
        Err(error) => {
            throw_io_exception(&mut env, error);
            JObjectArray::default()
        }
    }
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_drop<'a>(
    mut _env: JNIEnv<'a>,
    _this: JObject<'a>,
    ptr: jlong,
) {
    drop_manager(ptr);
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_BatteryImpl_update0<'a>(
    mut env: JNIEnv<'a>,
    this: JObject<'a>,
) {
    if let Err(error) = update_battery(&mut env, &this) {
        throw_io_exception(&mut env, error);
    }
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_dropBattery<'a>(
    mut _env: JNIEnv<'a>,
    _this: JObject<'a>,
    ptr: jlong,
) {
    drop_battery(ptr)
}
