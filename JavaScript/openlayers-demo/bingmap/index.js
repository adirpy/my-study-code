import 'ol/ol.css';
import Map from 'ol/Map';
import View from 'ol/View';
import TileLayer from 'ol/layer/Tile';
import BingMaps from 'ol/source/BingMaps';

var styles = [
    'RoadOnDemand',
    'Aerial',
    'AerialWithLabelsOnDemand',
    'CanvasDark',
    'OrdnanceSurvey'
];

var layers = [];
var i, ii;

for (i = 0, ii = styles.length; i < ii; ++i) {
    layers.push(
        new TileLayer(
            {
                visible: false,
                preload: Infinity,
                source: new BingMaps(
                    {
                        imagerySet : styles[i]
                    }
                )
            }
        )
    );
}

const map = new Map(
    {
        layers: layers,
        target: 'map',
        view: new View(
            {
                center: [-6655.5402445057125, 6709968.258934638],
                zoom: 13
            }
        )
    }
);

var select = document.getElementById('layer-select');
function onChange() {
    var style = select.nodeValue;
    for(var i = 0, ii = layers.length; i < ii; ++i) {
        layers[i].setVisible(styles[i] === style);
    }
}

select.addEventListener('change', onChange);
onChange();