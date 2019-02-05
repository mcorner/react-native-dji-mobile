//
//  KeyManager.swift
//  ReactNativeDjiMobile
//
//  Created by Adam Rosendorff on 2019/02/04.
//  Copyright © 2019 Facebook. All rights reserved.
//

import Foundation
import DJISDK

@objc(DJIKeyManagerWrapper)
class DJIKeyManagerWrapper: RCTEventEmitter {
  
  @objc(getValueForKey:resolve:reject:)
  func getValueForKey(DJIKeyString: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    let key = DJIProductKey(param: DJIKeyString)!
    DJISDKManager.keyManager()?.getValueFor(key, withCompletion: { (newValue: DJIKeyedValue?, error: Error?) in
      if (newValue != nil) {
        NSLog("KEYLISTENER: %@", newValue!.stringValue!)
      } else {
        NSLog("KEYLISTENER INVALID VALUE!")
      }
    })
  }
  
  
  @objc(startListeningForChangesOnKey:resolve:reject:)
  func startListeningForChangesOnKey(DJIKeyString: String, resolve: RCTPromiseResolveBlock, reject: RCTPromiseRejectBlock) {
    
    NSLog("KEYLISTENER: %@", DJIKeyString)
    
    let splitKey = DJIKeyString.split(separator: ".")
    if (splitKey.count != 2) {
      reject("Start Listener Error", "Invalid Key Supplied", nil)
    }
    let keyPrefix = String(splitKey[0])
    let keyParam = String(splitKey[1])
    
    var key: DJIKey
    
    switch keyPrefix {
    case "DJIProductKey":
      key = DJIProductKey(param: keyParam)!
    case "DJIFlightControllerKey":
      key = DJIFlightControllerKey(param: keyParam)!
    default:
      // TODO: (Adam) Do something here!
      key = DJIProductKey(param: keyParam)!
    }
    
    DJISDKManager.keyManager()?.startListeningForChanges(on: key, withListener: self, andUpdate: { (oldValue: DJIKeyedValue?, newValue: DJIKeyedValue?) in
      if newValue != nil {
        self.sendEvent(withName: "keyEvent", body: [
          "type": DJIKeyString,
          "value": newValue!.stringValue!
          ])
      }
    })
    resolve(nil)
    
  }
  
  //  @objc(supportedEvents)
  override func supportedEvents() -> [String]! {
    return ["keyEvent"]
  }
  
  @objc static override func requiresMainQueueSetup() -> Bool {
    return false
  }
  
}
