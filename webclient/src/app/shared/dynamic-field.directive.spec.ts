import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Component } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { DynamicFieldDirective } from './dynamic-field.directive';

@Component( {
    selector: 'app-directive-test',
    template: '<div appDynamicField [config]="field" [group]="form"></div>'
} )
class TestDynamicFieldDirectiveComponent {

    field: Object = {};
    form: FormGroup;

    constructor() {
    }
}

describe( 'shared.DynamicFieldDirective', () => {

    let component: TestDynamicFieldDirectiveComponent;
    let fixture: ComponentFixture<TestDynamicFieldDirectiveComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule( {
            declarations: [TestDynamicFieldDirectiveComponent, DynamicFieldDirective]
        } );
        fixture = TestBed.createComponent( TestDynamicFieldDirectiveComponent );
        component = fixture.componentInstance;
    } );

    it( 'should create an instance', () => {
        expect( component ).toBeTruthy();
    } );

} );

