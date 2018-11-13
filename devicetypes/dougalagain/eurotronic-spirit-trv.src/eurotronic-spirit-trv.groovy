/**
 *  Eurotronic Spirit TRV
 *
 *  Copyright 2018 Ed Cann
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  With Massive thanks to: 
 *      SmartThings Fibaro Dimmer 2 Handler
 *      Danfoss Living Connect Radiator Thermostat LC-13 by Tom Philip
 *      SmartThings Gneric Z-Wave Thermostat
 *  without which i would never have figured this out
 */
metadata {
	definition (name: "Eurotronic Spirit TRV", namespace: "dougalAgain", author: "Ed Cann") {
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Battery"
        capability "Actuator"
        capability "Refresh"
        capability "Sensor"
        capability "Configuration"
        capability "Polling"

        command "lock"
        command "unlock"
        command "boost"
        command "boostoff"
        command "ecoheat"
        command "ecooff"
        command "heat"
        command "off"
		command "temperatureUp"
		command "temperatureDown"


    fingerprint type: "0806", mfr: "0148", prod: "0003", model: "0001", cc: "20,80,70,72,31,26,71,75,98,40,43,86"
        
		// 0x20 = Basic v1
        // 0x80 = Battery v1
		// 0x70 = Configuration v1
		// 0x72 = Manufacturer Specific v1
		// 0x31 = Multilevel Sensor v5
        // 0x26 = MultiLevel Switch v1
        // 0x71 = Notification v8
		// 0x75 = Protection v2
        // 0x98 = Security v2
        // 0x40 = Thermostat Mode
		// 0x43 = Thermostat Setpoint v3
		// 0x86 = Version v1
	}

	simulator {

	}
	tiles(scale: 2) {

		multiAttributeTile(name:"richtemp", type:"thermostat", width:6, height:4) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState "default", label:'${currentValue}°', unit:"dC"
			}

			tileAttribute("device.nextHeatingSetpoint", key: "VALUE_CONTROL") {
				attributeState "VALUE_UP", action: "temperatureUp"
				attributeState "VALUE_DOWN", action: "temperatureDown"
			}

			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState "Eco", label:'Eco', backgroundColor:"#44b621", icon:"st.nest.nest-leaf"
				attributeState "Heating", label:"Heating", backgroundColor:"#d04e00", icon:"st.thermostat.heat"
                attributeState "Boost", label:"Boost", backgroundColor:"#bc2323", icon:"st.thermostat.heat"
				attributeState "Off", label:"Off", backgroundColor:"#cccccc", icon:"st.thermostat.heating-cooling-off"
			}

			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState "heat", label:"heat"
				attributeState "off", label:"off"
			}

			tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
				attributeState "default", label:'${currentValue}°', unit:"dC"
			}
		}

        standardTile("operatingState", "device.thermostatOperatingState", width: 2, height: 2) {
            state "esheating", label:'Energy Save Heating', backgroundColor:"#44b621", icon:"st.nest.nest-leaf"
			state "heating", label:"Heating", backgroundColor:"#d04e00", icon:"st.thermostat.heat"
            state "qheating", label:"Quick Heating", backgroundColor:"#bc2323", icon:"st.thermostat.heat"
			state "off", label:"Off", backgroundColor:"#cccccc", icon:"st.thermostat.heating-cooling-off"
		}
		standardTile("switcher", "device.switch", height: 2, width: 2, decoration: "flat") {
			state "off", action:"on", label: "off", icon: "st.thermostat.heating-cooling-off", backgroundColor:"#ffffff"
			state "on", action:"off", label: "on", icon: "st.thermostat.heat", backgroundColor:"#00a0dc"
		}
        valueTile("temperature", "device.temperature", inactiveLabel: false, height: 2, width: 2,
                decoration: "flat") {
                state "temperature", label:'${currentValue} °C', backgroundColor:"#d04e00", icon: "st.thermostat.heat"
        }
        valueTile("heatingSetpoint", "device.heatingSetpoint", inactiveLabel: false, height: 2, width: 2,
                decoration: "flat") {
                state "heatingSetpoint", label:'${currentValue} °C', backgroundColor:"#d04e00", icon: "st.thermostat.heat"
        }
        standardTile("refresh", "command.refresh", inactiveLabel: false, height: 2, width: 2,
                     decoration: "flat") {
                state "default", label:'', action:"refresh.refresh",
                      icon:"st.secondary.refresh"
        }

        valueTile("battery", "device.battery", inactiveLabel: false, height: 2, width: 2,
                decoration: "flat") {
                state "battery", icon:"https://raw.githubusercontent.com/tommysqueak/SmartThingsPublic/master/devicetypes/tommysqueak/danfoss-living-connect-radiator-thermostat.src/battery.png",  label:'${currentValue}%', unit:"%"
        }
		standardTile("boostMode", "device.thermostatMode", height: 2, width: 2, decoration: "flat") {
			state "default", action:"boost", icon: "st.thermostat.heat"
			state "qheating", action:"boostoff", icon: "st.thermostat.heat", backgroundColor:"#bc2323"
		}

		standardTile("ecoMode", "device.thermostatMode", height: 2, width: 2, decoration: "flat") {
			state "default", action:"ecoheat", label: "Eco", icon: "st.nest.nest-leaf"
			state "esheating", action:"ecooff", label: "Eco", icon: "st.nest.nest-leaf", backgroundColor:"#44b621"
		}

		standardTile("lockMode", "device.protectionState", height: 2, width: 2, decoration: "flat") {
			state "unlocked", action:"lock", icon: "st.tesla.tesla-locked"
			state "locked", action:"unlock", icon: "st.tesla.tesla-locked", backgroundColor:"#1e9cbb"
		}

		standardTile("turnOff", "device.thermostatMode", height: 2, width: 2, decoration: "flat") {
			state "off", action:"heat", icon: "st.thermostat.heating-cooling-off", backgroundColor:"#1e9cbb"
			state "default", action:"off", icon: "st.thermostat.heating-cooling-off"
		}
		standardTile("configureAfterSecure", "command.configureAfterSecure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configureAfterSecure.configureAfterSecure", icon:"st.secondary.configure"
		}


		main "temperature"
		details(["richtemp", "boostMode", "ecoMode", "lockMode", "turnOff", "refresh", "battery" ])
	}
 
	preferences {
        input "LCDinvert", "enum", title: "Invert LCD", options: ["No", "Yes"], defaultValue: "No", required: false, displayDuringSetup: true
        input "LCDtimeout", "number", title: "LCD Timeout (in secs)", description: "LCD will switch off after this time (5 - 30secs)", range: "5..30", displayDuringSetup: true
        input "ecoTemp", "number", title: "Eco Heat Temperature", description: "Temperature to heat to in Eco Mode (8 - 28°C)", range: "8..28", displayDuringSetup: false
        input "backlight", "enum", title: "Enable backlight", options: ["No", "Yes"], defaultValue: "No", required: false, displayDuringSetup: true
        input "windowOpen", "enum", title: "Window Open Detection",description: "Sensitivity of Open Window Detection", options: ["Disabled", "Low", "Medium", "High" ], defaultValue: "Medium", required: false, displayDuringSetup: false
        input "tempOffset", "number", title: "Temperature Offset", description: "Adjust the measured temperature (-5 to +5°C)", range: "-5..5", displayDuringSetup: false
    }    
}

def parse(String description) {
	//log.debug "Parsing '${description}'"

        def result = null
        def cmd = zwave.parse(description)
        if (cmd) {
                result = zwaveEvent(cmd)
        //        log.debug "Parsed ${cmd} to ${result.inspect()}"
        } else {
        //        log.debug "Non-parsed event: ${description}"
        }
        result
}


def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd)
{
        def map = [ displayed: true, value: cmd.scaledSensorValue.toString() ]
        switch (cmd.sensorType) {
                case 1:
                        map.name = "temperature"
                        map.unit = cmd.scale == 1 ? "F" : "C"
                        break;
                case 2:
                        map.name = "value"
                        map.unit = cmd.scale == 1 ? "%" : ""
                        break;
        }
        createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
        def map = [ name: "battery", unit: "%" ]
        if (cmd.batteryLevel == 0xFF) {  // Special value for low battery alert
                map.value = 1
                map.descriptionText = "${device.displayName} has a low battery"
                map.isStateChange = true
        } else {
                map.value = cmd.batteryLevel
        }
        createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
        def result = []
        if (cmd.nodeId.any { it == zwaveHubNodeId }) {
                result << createEvent(descriptionText: "$device.displayName is associated in group ${cmd.groupingIdentifier}")
        } else if (cmd.groupingIdentifier == 1) {
                // We're not associated properly to group 1, set association
                result << createEvent(descriptionText: "Associating $device.displayName in group ${cmd.groupingIdentifier}")
                result << response(zwave.associationV1.associationSet(groupingIdentifier:cmd.groupingIdentifier, nodeId:zwaveHubNodeId))
        }
        result
}

// Devices that support the Security command class can send messages in an
// encrypted form; they arrive wrapped in a SecurityMessageEncapsulation
// command and must be unencapsulated
def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
        def encapsulatedCommand = cmd.encapsulatedCommand([0x98: 2, 0x20: 1, 0x80: 1, 0x70: 1, 0x72: 1, 0x31: 5, 0x26: 1, 0x71: 8,0x75: 1, 0x98: 2, 0x40: 2, 0x43: 2, 0x86: 1 ])

        // can specify command class versions here like in zwave.parse
        if (encapsulatedCommand) {
                return zwaveEvent(encapsulatedCommand)
        }
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
        createEvent(descriptionText: "${device.displayName}: ${cmd}")
}
def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd ) {
    //log.debug "ConfigReport. cmd:${cmd}"
    cmd
}


def zwaveEvent(physicalgraph.zwave.commands.thermostatmodev2.ThermostatModeReport cmd ) {
    //log.debug "ModeReport. cmd:${cmd}"
    cmd
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv2.ThermostatSetpointReport cmd) {
	//	Parsed ThermostatSetpointReport(precision: 2, reserved01: 0, scale: 0, scaledValue: 21.00, setpointType: 1, size: 2, value: [8, 52])
    //	log.debug "ThermostatSetpoint. cmd:${cmd}"
    if(cmd.setpointType == 1) { //thisis the standard heating setpoint
        def radiatorSetPoint = cmd.scaledValue
        sendEvent(name:"nextHeatingSetpoint", value: radiatorSetPoint, unit: getTemperatureScale(), displayed: false)
        sendEvent(name:"heatingSetpoint", value: radiatorSetPoint.toString(), unit: getTemperatureScale(), displayed: true)
    }
    cmd
}

def temperatureUp() {
    //log.debug(device.currentValue("nextHeatingSetpoint").toBigDecimal())
	def nextTemp = device.currentValue("nextHeatingSetpoint").toBigDecimal() + 0.5
	// It can't handle above 28, so don't allow it go above
	// TODO: deal with Farenheit?
	if(nextTemp > 28) {
		nextTemp = 28
	}
    //runIn(5, "setHeatingSetpoint", [data: nexttemp, overwrite: true])
	setHeatingSetpoint(nextTemp)
}

def temperatureDown() {
	def nextTemp = device.currentValue("nextHeatingSetpoint").toBigDecimal() - 0.5
	// It can't go below 4, so don't allow it
	if(nextTemp < 8) {
		nextTemp = 8
	}
	setHeatingSetpoint(nextTemp)
}

def setHeatingSetpoint(degrees) {
	//log.debug "Setting Temp to ${degrees}"
	sendEvent(name:"nextHeatingSetpoint", value: degrees, unit: getTemperatureScale(), descriptionText: "Next heating setpoint is ${degrees}")
    def valueDegrees = degrees*10
    secureSequence([
        zwave.thermostatSetpointV1.thermostatSetpointSet (precision: 1, scale: 0, scaledValue: degrees, setpointType: 1, size: 2, value: [0, valueDegrees.toInteger()]),
    ])
}


def lock() {
	   log.trace("lock")    
    sendEvent(name: "protectionState", value: "locked", displayed: false)
    secureSequence([
        zwave.protectionV1.protectionSet(protectionState: 1),
        zwave.protectionV1.protectionGet()
    ])
}

def unlock() {
	   log.trace("unlock")    
        sendEvent(name: "protectionState", value: "unlocked", displayed: false)
    secureSequence([
        zwave.protectionV1.protectionSet(protectionState: 0),
        zwave.protectionV1.protectionGet()
    ])
}

def boost() {
        log.trace("Boost")    
        sendEvent(name: "thermostatMode", value: "qheating", displayed: false)
        sendEvent(name: "thermostatOperatingState", value: "Boost", displayed: false)
        secureSequence([
            zwave.thermostatModeV2.thermostatModeSet(mode: 0x0F),
            zwave.thermostatModeV2.thermostatModeGet()
        ])
}

def boostoff() {
        log.trace("Boost Off")    
        sendEvent(name: "thermostatMode", value: "default", displayed: false)
        sendEvent(name: "thermostatOperatingState", value: "Heating", displayed: false)
        secureSequence([
            zwave.thermostatModeV2.thermostatModeSet(mode: 0x01),
            zwave.thermostatModeV2.thermostatModeGet()
        ])
}

def ecoheat() {
        log.trace("Eco Heat")    
        sendEvent(name: "thermostatMode", value: "esheating", displayed: false)
        sendEvent(name: "thermostatOperatingState", value: "Eco", displayed: false)
        secureSequence([
            zwave.thermostatModeV2.thermostatModeSet(mode: 0x0B),
            zwave.thermostatModeV2.thermostatModeGet()
        ])
}

def ecooff() {
        log.trace("Eco Off")    
        sendEvent(name: "thermostatMode", value: "default", displayed: false)
        sendEvent(name: "thermostatOperatingState", value: "Heating", displayed: false)
        secureSequence([
            zwave.thermostatModeV2.thermostatModeSet(mode: 0x01),
            zwave.thermostatModeV2.thermostatModeGet()
        ])
}

def on() {
    heat()
}

def heat() {
        log.trace("Heat")    
        sendEvent(name: "thermostatMode", value: "default", displayed: false)
        sendEvent(name: "thermostatOperatingState", value: "Heating", displayed: false)
        secureSequence([
            zwave.thermostatModeV2.thermostatModeSet(mode: 0x01),
            zwave.thermostatModeV2.thermostatModeGet()
        ])
}

def off() {
        log.trace("Off")    
        sendEvent(name: "thermostatMode", value: "off", displayed: false)
        sendEvent(name: "thermostatOperatingState", value: "Off", displayed: false)
        secureSequence([
            zwave.thermostatModeV2.thermostatModeSet(mode: 0x00),
            zwave.thermostatModeV2.thermostatModeGet()
        ])
}

def refresh() {
        // Some examples of Get commands
	   log.trace("refresh")
	   secureSequence([
                zwave.configurationV1.configurationSet(configurationValue:  LCDinvert == "Yes" ? [0x01] : [0x00], parameterNumber:1, size:1, scaledConfigurationValue:  LCDinvert == "Yes" ? 0x01 : 0x00),
                zwave.configurationV1.configurationSet(configurationValue: LCDtimeout == null ? [0] : [LCDtimeout], parameterNumber:2, size:1, scaledConfigurationValue: LCDtimeout == null ? 0 :  LCDtimeout),
                zwave.configurationV1.configurationSet(configurationValue:  backlight == "Yes" ? [0x01] : [0x00], parameterNumber:3, size:1, scaledConfigurationValue:  backlight == "Yes" ? 0x01 : 0x00),
                zwave.configurationV1.configurationSet(configurationValue:  windowOpen == "Low" ? [0x01] : windowOpen == "Medium" ? [0x02] : windowOpen == "High" ? [0x03] : [0x00], parameterNumber:7, size:1, scaledConfigurationValue:  windowOpen == "Low" ? 0x01 : windowOpen == "Disabled" ? 0x00 : windowOpen == "High" ? 0x03 : 0x02),
                zwave.configurationV1.configurationSet(configurationValue: tempOffset == null ? [0] : [tempOffset*10], parameterNumber:8, size:1, scaledConfigurationValue: tempOffset == null ? 0 : tempOffset*10),
                zwave.thermostatSetpointV1.thermostatSetpointSet(precision: 1, reserved01: 0, scale: 0, scaledValue: ecoTemp == null ? 8 : ecoTemp, setpointType: 11, size: 2, value: ecoTemp == null ? [0, 80] : [0, ecoTemp*10]),
                zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:1, scale:1),  // get temp
                zwave.thermostatModeV2.thermostatModeGet(),
                zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 0x01),
                zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 0x0B),
                zwave.configurationV1.configurationGet(parameterNumber:1),
                zwave.configurationV1.configurationGet(parameterNumber:2),
                zwave.configurationV1.configurationGet(parameterNumber:3),
                zwave.configurationV1.configurationGet(parameterNumber:7),
                zwave.configurationV1.configurationGet(parameterNumber:8),
                zwave.batteryV1.batteryGet(),
        ])
}

        //input "ecoTemp", "number", title: "Eco Heat Temperature", description: "Temperature to heat to in Eco Mode (8 - 28°C)", range: "8..28", displayDuringSetup: false
            
def updated() {
    if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 2000) {
        state.updatedLastRanAt = now()
        log.trace ("updated config")
        // submitting the info here doesn't reach the device
    //    secureSequence([
    //            zwave.configurationV1.configurationGet(parameterNumber:2),
    //            zwave.configurationV1.configurationGet(parameterNumber: 1), 
    //            zwave.configurationV1.configurationSet(configurationValue: [0x00], parameterNumber:1, size:1, scaledConfigurationValue:0x00),
    //            zwave.configurationV1.configurationGet(parameterNumber: 1), 
    //    ])
    }
    else
    {
        log.trace "update ran within the last 2 seconds"
    }
}


// If you add the Polling capability to your device type, this command
// will be called approximately every 5 minutes to check the device's state
def poll() {
	log.trace("poll")
	secureSequence([
                zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:1, scale:1),  // get temp
                zwave.thermostatModeV2.thermostatModeGet(),
                zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 0x01),
                zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 0x0B),
                zwave.configurationV1.configurationGet(parameterNumber:1),
                zwave.configurationV1.configurationGet(parameterNumber:2),
                zwave.configurationV1.configurationGet(parameterNumber:3),
                zwave.configurationV1.configurationGet(parameterNumber:7),
                zwave.configurationV1.configurationGet(parameterNumber:8),
                zwave.batteryV1.batteryGet(),
	])
}

// If you add the Configuration capability to your device type, this
// command will be called right after the device joins to set
// device-specific configuration commands.
def configure() {
        
}

private secure(physicalgraph.zwave.Command cmd) {
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

private secureSequence(commands, delay=500) {
	delayBetween(commands.collect{ secure(it) }, delay)
}
