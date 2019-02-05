import {
  Platform,
} from 'react-native';

import DJISDKManager from './lib/DJISDKManager';
import DJIKeyManager from './lib/DJIKeyManager';
import {
  DJIProductKey,
  DJIFlightControllerKey,
} from './lib/DJIKeys';

if (Platform.OS === 'ios') {
  // console.log('ios');
} else if (Platform.OS === 'android') {
  // console.log('android');
} else {
  throw new Error('Unsupported platform! Only iOS or Android is currently supported');
}

export {
  DJISDKManager,
  DJIKeyManager,
  DJIProductKey,
  DJIFlightControllerKey,
}
