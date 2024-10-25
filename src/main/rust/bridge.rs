use jni::{
    objects::{JClass, JObject, JObjectArray},
    sys::jlong,
    JNIEnv,
};
use starship_battery::{Battery, Manager};

use crate::{create_manager, get_batteries, update_battery, util::drop_box};

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_create<'a>(
    mut env: JNIEnv<'a>,
    _class: JClass<'a>,
) -> jlong {
    match create_manager() {
        Ok(ptr) => return ptr,
        Err(error) => error.throw(&mut env),
    }

    jlong::default()
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_batteries0<'a>(
    mut env: JNIEnv<'a>,
    this: JObject<'a>,
) -> JObjectArray<'a> {
    match get_batteries(&mut env, &this) {
        Ok(batteries) => return batteries,
        Err(error) => error.throw(&mut env),
    }

    JObjectArray::default()
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_drop<'a>(
    mut _env: JNIEnv<'a>,
    _this: JObject<'a>,
    ptr: jlong,
) {
    drop_box::<Manager>(ptr);
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_BatteryImpl_update0<'a>(
    mut env: JNIEnv<'a>,
    this: JObject<'a>,
) {
    if let Err(error) = update_battery(&mut env, &this) {
        error.throw(&mut env);
    }
}

#[no_mangle]
pub extern "system" fn Java_net_lostluma_battery_impl_ManagerImpl_dropBattery<'a>(
    mut _env: JNIEnv<'a>,
    _this: JObject<'a>,
    ptr: jlong,
) {
    drop_box::<Battery>(ptr);
}
