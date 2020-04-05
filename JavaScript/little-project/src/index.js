import _ from 'lodash';
import PrintMe from './print.js';

function component() {
    const element = document.createElement('div');
    const btn = document.createElement('button');

    element.innerHTML = _.join(['hello', 'webpack'], ' ');
    
    btn.innerHTML = 'Click me and check the console!';
    btn.onclick = PrintMe;

    element.appendChild(btn);

    

    return element;
}

document.body.appendChild(component());

if (module.hot) {
    module.hot.accept('./print.js', function() {
        console.log('Accepting the updated printMe module');
        PrintMe();
    });
}