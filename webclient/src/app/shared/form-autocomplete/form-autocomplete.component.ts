/* tslint:disable:no-access-missing-member */
// autocomplete creates a #localVariable, which 'lint' then finds as
// a property that you're trying to access but does not exist in the class declaration.
// therefore turn off that rule for this file
import { Component, ViewChild, OnInit, AfterViewInit } from '@angular/core';
import { FormGroup, AbstractControl } from '@angular/forms';
import { MatAutocompleteSelectedEvent, MatAutocompleteTrigger } from '@angular/material';

import { Observable } from 'rxjs/Observable';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/startWith';
import 'rxjs/add/operator/map';

import { autocompleteMapper } from '@appstate/models/autocompletemapper.object';
import { DynamicSearchService } from '@appcore/services/dynamicsearch.service';
import { settings } from '@appcore/settings';

@Component( {
    selector: 'app-form-autocomplete',
    templateUrl: './form-autocomplete.component.html',
    styleUrls: ['./form-autocomplete.component.scss']
} )
export class FormAutocompleteComponent implements OnInit, AfterViewInit {

    config;
    group: FormGroup;
    searchResults: Observable<any[]>;
    @ViewChild( 'autoCompleteInput', { read: MatAutocompleteTrigger } ) trigger: MatAutocompleteTrigger;

    private displayCtrl: AbstractControl;
    private searchUpdated: Subject<string> = new Subject<string>();
    private selectedOption = {
        key: '',
        label: ''
    };

    constructor( private dynSearchService: DynamicSearchService<any> ) { }

    ngOnInit() {
        this.displayCtrl = this.group.get( this.config.name );
        this.registerSearchObservable();
    }

    ngAfterViewInit() {
        this.registerSelectOnBlur();
    }

    search() {
        this.searchUpdated.next( this.displayCtrl.value );
    }

    onOptionSelected( evt: MatAutocompleteSelectedEvent ) {
        this.selectedOption.key = evt.option.value;
        this.selectedOption.label = evt.option.viewValue;
    }

    displaySelection( key ) {
        if ( !key ) {
            return '';
        }

        if ( key === this.selectedOption.key && this.selectedOption.label ) {
            return this.selectedOption.label;
        }

        this.dynSearchService.lookup( key, this.config.lookupApi )
            .subscribe( c => {
                this.selectedOption.key = key;
                this.selectedOption.label = this.getLabel( c );
                this.displayCtrl.setValue( this.selectedOption.key );
            } );

        return '...';
    }

    getKey( entity: Object ) {
        return autocompleteMapper[this.config.lookupApi].getKey( entity );
    }

    getLabel( entity: Object ) {
        return autocompleteMapper[this.config.lookupApi].getLabel( entity );
    }

    private doSearch() {
        if ( this.displayCtrl.value ) {
            const needle = this.displayCtrl.value.replace( /[^\*](\s|$)/g, '* ' );
            this.searchResults = this.dynSearchService.search( needle, this.config.searchApi );
            this.searchResults.subscribe( results => {
                if ( results.length > 0 ) {
                    this.selectedOption.key = this.getKey( results[0] );
                    this.selectedOption.label = this.getLabel( results[0] );
                } else {
                    this.selectedOption.key = this.selectedOption.label = undefined;
                }
            } );
        }
    }

    private registerSearchObservable() {
        this.searchUpdated.asObservable()
            .debounceTime( settings.searchDebounce )
            .distinctUntilChanged()
            .subscribe(() => {
                this.doSearch();
            } );
    }

    private registerSelectOnBlur() {
        this.trigger.panelClosingActions
            .subscribe( e => {
                if ( !this.trigger.activeOption && this.selectedOption.key ) {
                    this.displayCtrl.setValue( this.selectedOption.key );
                    this.trigger.writeValue( this.selectedOption.key );
                } else {
                    this.displayCtrl.setValue( null );
                }
                this.searchUpdated.next( '' );
                this.trigger.closePanel();
            } )
    }
}
