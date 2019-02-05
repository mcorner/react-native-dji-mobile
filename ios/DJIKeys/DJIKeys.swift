//
//  DJISDK.swift
//  ReactNativeDjiMobile
//
//  Created by Adam Rosendorff on 2019/01/29.
//  Copyright © 2019 Facebook. All rights reserved.

import Foundation
import DJISDK

@objc(DJIProductKeyWrapper)
class DJIProductKeyWrapper: NSObject {
  let prefix = "DJIProductKey."
  
  @objc(constantsToExport)
  func constantsToExport() -> [String: Any]! {
    return [
      "DJIParamConnection": prefix + DJIParamConnection,
      "DJIParamSerialNumber": prefix + DJIParamSerialNumber,
      "DJIDefaultProduct": prefix + DJIDefaultProduct,
      "DJIParamFirmwareVersion": prefix + DJIParamFirmwareVersion,
      "DJIProductParamModelName": prefix + DJIProductParamModelName,
    ]
  }
  
  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
}

@objc(DJIFlightControllerKeyWrapper)
class DJIFlightControllerKeyWrapper: NSObject {
  let prefix = "DJIFlightControllerKey."
  
  @objc(constantsToExport)
  func constantsToExport() -> [String: Any]! {
    return [
      "DJIFlightControllerParamVelocity": prefix + DJIFlightControllerParamVelocity,
    ]
  }
  
  @objc static func requiresMainQueueSetup() -> Bool {
    return true
  }
  
}
