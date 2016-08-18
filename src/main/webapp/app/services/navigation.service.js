/**
 * Created by Sergej on 18.08.16.
 */
(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .factory('NavigationService', NavigationService);

    NavigationService.$inject = ['$state', 'Auth', 'Principal', 'ProfileService', 'LoginService'];

    function NavigationService ($state, Auth, Principal, ProfileService, LoginService) {
        return {
            "addBook": function addBook(){
                $state.go('book/new',{isbn:"124525313453"});
            }
        }


    }
})();
