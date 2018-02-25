import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { CodekeysComponent } from './codekeys.component';
import { TranslatorService } from '../translator.service';

describe( 'CodekeysComponent', () => {
    let component: CodekeysComponent;
    let fixture: ComponentFixture<CodekeysComponent>;
    const mockLoginService = jasmine.createSpyObj( 'TranslatorService', ['add', 'lookup'] );

    beforeEach( async(() => {
        TestBed.configureTestingModule( {
            declarations: [CodekeysComponent],
            providers: [
                { provide: TranslatorService, useValue: mockLoginService }
            ]
        } )
            .compileComponents();
    } ) );

    beforeEach(() => {
        fixture = TestBed.createComponent( CodekeysComponent );
        component = fixture.componentInstance;
        fixture.detectChanges();
    } );

    it( 'should create', () => {
        expect( component ).toBeTruthy();
    } );
} );
