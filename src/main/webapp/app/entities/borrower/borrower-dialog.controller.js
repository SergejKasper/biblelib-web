(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .controller('BorrowerDialogController', BorrowerDialogController);

    BorrowerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Borrower', 'HasBook'];

    function BorrowerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Borrower, HasBook) {
        var vm = this;

        vm.borrower = entity;
        vm.clear = clear;
        vm.save = save;
        vm.hasbooks = HasBook.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.borrower.id !== null) {
                Borrower.update(vm.borrower, onSaveSuccess, onSaveError);
            } else {
                Borrower.save(vm.borrower, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('bibelBibliothekApp:borrowerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
