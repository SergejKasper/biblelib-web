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

        vm.hasBooks = [];
        vm.noHasBookDetails = true;

        vm.borrower.books.forEach(function(book){
            return HasBook.get({id : book.id}).$promise.then(function(resp){
                vm.hasBooks.push(resp);
            });
        });

        var unsubscribe = $rootScope.$on('bibelBibliothekApp:borrowerUpdate', function(event, result) {
            vm.borrower = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
