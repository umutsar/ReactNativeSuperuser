// could be any js file
/* ... */
import { /* ... */ NativeModules } from 'react-native';




/* ... */

const MyRootModule = NativeModules.MyRootModule; // MyRootModule should be your module name


const handleButtonClick = async () => {
    try {
        const output = await MyRootModule.runCommand('ls /sdcard'); // you can write any shell command
    } catch (error) {
        console.error('An error occurred while running the command:', error);
    }
};

handleButtonClick();

/* ... */
