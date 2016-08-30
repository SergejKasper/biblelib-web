(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('app', {
            abstract: true,
            views: {
                'navbar@': {
                    templateUrl: 'app/layouts/navbar/navbar.html',
                    controller: 'NavbarController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                authorize: ['Auth',
                    function (Auth) {
                        return Auth.authorize();
                    }
                ],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('global');
                }]
                /*, apiRegistration: ['$http', function($http){
                    $http.post("https://openlibrary.org/account/login", {"username": "sergejkasper", "password": "niceapi4books"}, {
                        "Content-Type": "application/json"
                    });
                }]*/
            }
        });
    }
})();
