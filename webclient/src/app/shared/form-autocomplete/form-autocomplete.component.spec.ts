import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import { MatInputModule, MatAutocompleteModule } from '@angular/material';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

import { DynamicSearchService } from '@appcore/services/dynamicsearch.service';
import { FormAutocompleteComponent } from './form-autocomplete.component';

describe( 'shared.FormAutocompleteComponent', () => {
    let component: FormAutocompleteComponent;
    let fixture: ComponentFixture<FormAutocompleteComponent>;
    const mockService = jasmine.createSpyObj( 'DynamicSearchService', ['search', 'lookup'] );

    beforeEach( async(() => {
        TestBed.configureTestingModule( {
            declarations: [FormAutocompleteComponent],
            providers: [{ provide: DynamicSearchService, useValue: mockService }],
            imports: [NoopAnimationsModule, ReactiveFormsModule, MatInputModule, MatAutocompleteModule]
        } )
            .compileComponents();
    } ) );

    beforeEach(() => {
        fixture = TestBed.createComponent( FormAutocompleteComponent );
        component = fixture.componentInstance;
        component.config = {
            name: 'foobar',
            formControl: 'text',
            label: 'My foobar'
        };

        const group = new FormGroup( {} );
        group.addControl( 'foobar', new FormControl() );
        component.group = group;
        fixture.detectChanges();
    } );

    it( 'should create', () => {
        expect( component ).toBeTruthy();
    } );
} );
