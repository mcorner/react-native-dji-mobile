
import {
  NativeModules,
} from 'react-native';

import {
  usePromiseOrCallback,
} from '../utilities';

const {
  DJIProductKeyWrapper,
  DJIFlightControllerKeyWrapper,
} = NativeModules;

export {
  DJIProductKeyWrapper as DJIProductKey,
  DJIFlightControllerKeyWrapper as DJIFlightControllerKey,
}
