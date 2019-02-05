// @flow strict

// const usePromiseOrCallback = async (funcToCall: Function, callback: (err: ?string, response: any) => void, ...args: any) => {
const usePromiseOrCallback = async (funcToCall: Function, callback: (err: ?string, response: any) => void, ...args: any) => {
  if (typeof callback === 'function') {
    console.log('');
    try {
      const response = await funcToCall(...args);
      callback(null, response);
    } catch (err) {
      callback(err, null);
    }
  } else {
    // Return the called function's promise (all native code should return a promise)
    return funcToCall(...args);
  }
};

export {
  usePromiseOrCallback
};
