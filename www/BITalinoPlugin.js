/* global cordova, plugin defined by js-module in plugin.xml so no need to define require and module here */

module.exports = {
    
    showToast: function(aString, successCallback, errorCallback){ 
        cordova.exec(successCallback, errorCallback, "BITalinoPlugin", "showToast", [aString]);
    },

    greet: function(name, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "BITalinoPlugin", "greet", [name]);
    },

    checkBitalino: function(str, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "BITalinoPlugin", "checkBitalino", [str]);
    },

    startBitalino: function(str, successCallback, errorCallback){
    	cordova.exec(successCallback, errorCallback, "BITalinoPlugin", "startBitalino", [str]);
    }

};