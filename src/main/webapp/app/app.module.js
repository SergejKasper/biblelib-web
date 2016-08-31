(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp', [
            'wu.masonry',
            'ngStorage',
            'tmh.dynamicLocale',
            'pascalprecht.translate',
            'ngResource',
            'ngCookies',
            'ngAria',
            'ngCacheBuster',
            'ngFileUpload',
            'ui.bootstrap',
            'ui.bootstrap.datetimepicker',
            'ui.router',
            'infinite-scroll',
            // jhipster-needle-angularjs-add-module JHipster will add new module here
            'angular-loading-bar'
        ])
        .run(run);

    run.$inject = ['stateHandler', 'translationHandler', 'printHandler'];

    function run(stateHandler, translationHandler, printHandler) {
        stateHandler.initialize();
        translationHandler.initialize();
        printHandler.initialize();
    }
})();
