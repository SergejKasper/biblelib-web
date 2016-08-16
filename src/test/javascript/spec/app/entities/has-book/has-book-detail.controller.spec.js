'use strict';

describe('Controller Tests', function() {

    describe('HasBook Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockHasBook, MockBorrower, MockBook;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockHasBook = jasmine.createSpy('MockHasBook');
            MockBorrower = jasmine.createSpy('MockBorrower');
            MockBook = jasmine.createSpy('MockBook');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'HasBook': MockHasBook,
                'Borrower': MockBorrower,
                'Book': MockBook
            };
            createController = function() {
                $injector.get('$controller')("HasBookDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'bibelBibliothekApp:hasBookUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
