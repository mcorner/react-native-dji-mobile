//
//  KeyManagerWrapper-bridge.m
//  ReactNativeDjiMobile
//
//  Created by Adam Rosendorff on 2019/02/04.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(DJIKeyManagerWrapper, RCTEventEmitter)

RCT_EXTERN_METHOD(
                  getValueForKey: (NSString)DJIKeyString
                  resolve:        (RCTPromiseResolveBlock)resolve
                  reject:         (RCTPromiseRejectBlock)reject
                  )

RCT_EXTERN_METHOD(
                  startListeningForChangesOnKey: (NSString)DJIKeyString
                  resolve:                       (RCTPromiseResolveBlock)resolve
                  reject:                        (RCTPromiseRejectBlock)reject
                  )

@end
