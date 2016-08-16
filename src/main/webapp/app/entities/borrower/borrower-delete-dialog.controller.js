(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BorrowerDeleteController',BorrowerDeleteController);

    BorrowerDeleteController.$inject = ['$uibModalInstance', 'entity', 'Borrower'];

    function BorrowerDeleteController($uibModalInstance, entity, Borrower) {
        var vm = this;

        vm.borrower = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Borrower.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
