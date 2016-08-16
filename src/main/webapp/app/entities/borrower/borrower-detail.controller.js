(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BorrowerDetailController', BorrowerDetailController);

    BorrowerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Borrower', 'HasBook'];

    function BorrowerDetailController($scope, $rootScope, $stateParams, previousState, entity, Borrower, HasBook) {
        var vm = this;

        vm.borrower = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('bibelBibliothekApp:borrowerUpdate', function(event, result) {
            vm.borrower = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
