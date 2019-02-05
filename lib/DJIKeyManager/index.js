
import {
  NativeModules,
  NativeEventEmitter,
} from 'react-native';

import {
  usePromiseOrCallback,
} from '../utilities';

import {
  Subject,
} from 'rxjs'

import {
  filter,
} from 'rxjs/operators';


const {
  DJIKeyManagerWrapper,
} = NativeModules;

const DJIKeyManager = {
  async startListeningForChangesOnKey(DJIKeyString: string, callback) {
    // TODO: (Adam) implement this so that it will reject if the string is invalid and use either a callback or promise!
    await DJIKeyManagerWrapper.startListeningForChangesOnKey(DJIKeyString);
    return DJIKeyEventSubject.pipe(filter(evt => evt.type === DJIKeyString)).asObservable();
    // return usePromiseOrCallback(async () => {
    //   await DJIKeyManagerWrapper.startListeningForChangesOnKey(DJIKeyString);
    //   return DJIKeyEventSubject.filter(evt => evt.type === DJIKeyString).asObservable();
    // }, callback);
  },
  getValueForKey(DJIKeyString: string, callback) {
    return usePromiseOrCallback(DJIKeyManagerWrapper.getValueForKey, callback, DJIKeyString);
  }
};

const DJIKeyManagerEmitter = new NativeEventEmitter(DJIKeyManagerWrapper);

const DJIKeyEventSubject = new Subject();

const DJIKeyEventListener = DJIKeyManagerEmitter.addListener('keyEvent', evt => {
  DJIKeyEventSubject.next(evt);
});

export default DJIKeyManager;
