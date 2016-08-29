(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('borrower', {
            parent: 'entity',
            url: '/borrower',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'bibelBibliothekApp.borrower.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/borrower/borrowers.html',
                    controller: 'BorrowerController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('borrower');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('borrower-detail', {
            parent: 'entity',
            url: '/borrower/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'bibelBibliothekApp.borrower.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/borrower/borrower-detail.html',
                    controller: 'BorrowerDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('borrower');
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('hasBook');
                    $translatePartialLoader.addPart('language');

                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Borrower', function($stateParams, Borrower) {
                    return Borrower.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'borrower',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('borrower-detail.edit', {
            parent: 'borrower-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrower/borrower-dialog.html',
                    controller: 'BorrowerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Borrower', function(Borrower) {
                            return Borrower.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('borrower.new', {
            parent: 'borrower',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrower/borrower-dialog.html',
                    controller: 'BorrowerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                email: null,
                                phoneNumber: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('borrower', null, { reload: true });
                }, function() {
                    $state.go('borrower');
                });
            }]
        })
        .state('borrower.edit', {
            parent: 'borrower',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrower/borrower-dialog.html',
                    controller: 'BorrowerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Borrower', function(Borrower) {
                            return Borrower.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('borrower', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('borrower.delete', {
            parent: 'borrower',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/borrower/borrower-delete-dialog.html',
                    controller: 'BorrowerDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Borrower', function(Borrower) {
                            return Borrower.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('borrower', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
