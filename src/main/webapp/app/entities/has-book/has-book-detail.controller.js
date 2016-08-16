(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('HasBookDetailController', HasBookDetailController);

    HasBookDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'HasBook', 'Borrower', 'Book'];

    function HasBookDetailController($scope, $rootScope, $stateParams, previousState, entity, HasBook, Borrower, Book) {
        var vm = this;

        vm.hasBook = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('bibelBibliothekApp:hasBookUpdate', function(event, result) {
            vm.hasBook = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
