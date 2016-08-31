/**
 * Created by Sergej on 31.08.16.
 */

(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .factory('printHandler', PrintHandler);

    PrintHandler.$inject = [];

    function PrintHandler () {
        var printers;
        var initialize = function(){
            dymo.label.framework.init(initializeSuccessCallback);
        }
        var initializeSuccessCallback = function(){
            dymo.label.framework.getPrintersAsync().then(function(p) {
                printers = p;
            });
        };

        var service = {
            initialize: initialize,
            print: print
        };

        return service;

        function print (barcodeNr) {
            if (printers === null || printers.length === 0){
                alert("Kein Drucker verfügbar");
                return;
            }
            var labelUri = document.location.origin + "content/BARCODE_LAYOUT.label";
            var label = dymo.label.framework.openLabelFile(labelUri);
            console.log(label.toString());
            var paramsXml = dymo.label.framework.createLabelWriterPrintParamsXml ({ copies: 1 });
            var labelSet = new dymo.label.framework.LabelSetBuilder();
            var record = labelSet.addRecord();
            record.setText("BARCODE", barcodeNr);
            dymo.label.framework.printLabel(printers[0].name, paramsXml, label, labelSet);
        }
    }
})();
