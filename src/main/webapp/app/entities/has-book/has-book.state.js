(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('has-book', {
            parent: 'entity',
            url: '/has-book',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'HasBooks'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/has-book/has-books.html',
                    controller: 'HasBookController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('has-book-detail', {
            parent: 'entity',
            url: '/has-book/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'HasBook'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/has-book/has-book-detail.html',
                    controller: 'HasBookDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'HasBook', function($stateParams, HasBook) {
                    return HasBook.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'has-book',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('has-book-detail.edit', {
            parent: 'has-book-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/has-book/has-book-dialog.html',
                    controller: 'HasBookDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['HasBook', function(HasBook) {
                            return HasBook.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('has-book.new', {
            parent: 'has-book',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/has-book/has-book-dialog.html',
                    controller: 'HasBookDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                borrowDate: null,
                                returnDate: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('has-book', null, { reload: true });
                }, function() {
                    $state.go('has-book');
                });
            }]
        })
        .state('has-book.edit', {
            parent: 'has-book',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/has-book/has-book-dialog.html',
                    controller: 'HasBookDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['HasBook', function(HasBook) {
                            return HasBook.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('has-book', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('has-book.delete', {
            parent: 'has-book',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/has-book/has-book-delete-dialog.html',
                    controller: 'HasBookDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['HasBook', function(HasBook) {
                            return HasBook.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('has-book', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
