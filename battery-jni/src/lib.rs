use std::result;

use jni::{
    objects::{JObject, JObjectArray, JValue},
    sys::{jfloat, jlong},
    JNIEnv,
};
use starship_battery::{
    units::{
        electric_potential::volt, energy::watt_hour, power::watt, ratio::percent,
        thermodynamic_temperature::degree_celsius, time::second,
    },
    Battery, Manager,
};
use util::{as_descriptor, get_enum_member, get_ptr, ToJString};

const STRING_CLASS: &str = "java/lang/String";
const IO_EXCEPTION_CLASS: &str = "java/io/IOException";

const STATE_ENUM: &str = "net/lostluma/battery/api/State";
const TECHNOLOGY_ENUM: &str = "net/lostluma/battery/api/Technology";

const BATTERY_CLASS: &str = "net/lostluma/battery/impl/BatteryImpl";
const MANAGER_CLASS: &str = "net/lostluma/battery/impl/ManagerImpl";

mod bridge;
mod util;

struct Error {
    message: String, // Used as IOError message
}

impl From<jni::errors::Error> for Error {
    fn from(value: jni::errors::Error) -> Self {
        Error {
            message: value.to_string(),
        }
    }
}

impl From<starship_battery::Error> for Error {
    fn from(value: starship_battery::Error) -> Self {
        Error {
            message: value.to_string(),
        }
    }
}

type Result<T> = result::Result<T, Error>;

fn create_manager() -> Result<i64> {
    let manager = Manager::new()?;
    Ok(Box::into_raw(Box::from(manager)) as jlong)
}

fn get_batteries<'a>(env: &mut JNIEnv<'a>, this: &JObject<'a>) -> Result<JObjectArray<'a>> {
    let ptr = get_ptr(env, this)?;
    let manager = unsafe { &mut *(ptr as *mut Manager) };

    let mut count = 0;
    let mut batteries: Vec<Battery> = Vec::new();

    for battery in manager.batteries()? {
        count += 1;
        batteries.push(battery?);
    }

    let class = env.find_class(BATTERY_CLASS)?;
    let array = env.new_object_array(count, &class, JObject::null())?;

    for (battery, index) in batteries.into_iter().zip(0..) {
        let object = create_battery(env, this, battery)?;
        update_battery(env, &object)?;

        env.set_object_array_element(&array, index, object)?;
    }

    Ok(array)
}

fn drop_manager<'a>(ptr: jlong) {
    #[allow(unused_variables)]
    let manager = unsafe { Box::from_raw(ptr as *mut Manager) };
}

fn create_battery<'a>(
    env: &mut JNIEnv<'a>,
    parent: &JObject<'a>,
    battery: Battery,
) -> Result<JObject<'a>> {
    let technology = get_enum_member(env, TECHNOLOGY_ENUM, battery.technology())?.l()?;

    let vendor = battery.vendor().to_jstring(env)?;
    let model = battery.model().to_jstring(env)?;
    let serial_number = battery.serial_number().to_jstring(env)?;

    let ptr = Box::into_raw(Box::from(battery)) as jlong;

    let class = env.find_class(BATTERY_CLASS)?;

    let object = env.new_object(
        &class,
        format!("(JL{MANAGER_CLASS};L{TECHNOLOGY_ENUM};L{STRING_CLASS};L{STRING_CLASS};L{STRING_CLASS};)V"),
        &[JValue::Long(ptr), JValue::Object(parent), JValue::Object(&technology), JValue::Object(&vendor), JValue::Object(&model), JValue::Object(&serial_number)]
    )?;

    Ok(object)
}

fn update_battery<'a>(env: &mut JNIEnv<'a>, this: &JObject<'a>) -> Result<()> {
    let parent = env
        .get_field(this, "manager", as_descriptor(MANAGER_CLASS))?
        .l()?;

    let battery = unsafe { &mut *(get_ptr(env, this)? as *mut Battery) };
    let manager = unsafe { &mut *(get_ptr(env, &parent)? as *mut Manager) };

    manager.refresh(battery)?;

    let state_of_charge = battery.state_of_charge().get::<percent>();
    let energy = battery.energy().get::<watt_hour>();
    let energy_full = battery.energy_full().get::<watt_hour>();
    let energy_rate = battery.energy_rate().get::<watt>();
    let energy_full_design = battery.energy_full_design().get::<watt_hour>();
    let voltage = battery.voltage().get::<volt>();
    let state_of_health = battery.state_of_health().get::<percent>();

    let state = get_enum_member(env, STATE_ENUM, battery.state())?;

    let temperature = match battery.temperature() {
        Some(value) => value.get::<degree_celsius>(),
        None => jfloat::NAN,
    };
    let cycle_count = match battery.cycle_count() {
        Some(value) => value.into(),
        None => jlong::MIN,
    };
    let time_to_full = match battery.time_to_full() {
        Some(value) => value.get::<second>(),
        None => jfloat::NAN,
    };
    let time_to_empty = match battery.time_to_empty() {
        Some(value) => value.get::<second>(),
        None => jfloat::NAN,
    };

    env.call_method(
        &this,
        "update0",
        format!("(FFFFFFFL{STATE_ENUM};FJFF)V"),
        &[
            JValue::Float(state_of_charge),
            JValue::Float(energy),
            JValue::Float(energy_full),
            JValue::Float(energy_rate),
            JValue::Float(energy_full_design),
            JValue::Float(voltage),
            JValue::Float(state_of_health),
            state.borrow(),
            JValue::Float(temperature),
            JValue::Long(cycle_count),
            JValue::Float(time_to_full),
            JValue::Float(time_to_empty),
        ],
    )?;

    Ok(())
}

fn drop_battery<'a>(ptr: jlong) {
    #[allow(unused_variables)]
    let battery = unsafe { Box::from_raw(ptr as *mut Battery) };
}

fn throw_io_exception<'a>(env: &mut JNIEnv<'a>, error: Error) {
    env.throw_new(IO_EXCEPTION_CLASS, error.message)
        .expect("throw exception");
}
