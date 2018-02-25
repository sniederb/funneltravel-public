// Do declare components, directives, and pipes in a shared module when those items will be re-used and
// referenced by the components declared in other feature modules.
// Do import all modules required by the assets in the SharedModule; for example, CommonModule and FormsModule.

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
    MatFormFieldModule, MatInputModule, MatAutocompleteModule, MatSelectModule, MatButtonModule, MatCheckboxModule,
    MatMenuModule, MatToolbarModule, MatTabsModule, MatIconModule, MatCardModule, MatProgressSpinnerModule,
    MatDialogModule, MatGridListModule, MatSlideToggleModule, MatPaginatorModule, MatStepperModule, MatTableModule
} from '@angular/material';

import { CoreModule } from '@appcore/core.module';
import { TranslatorModule } from '../translator/translator.module';

import { FormerrorComponent } from './formerror/formerror.component';
import { DynamicFormComponent } from './dynamic-form/dynamic-form.component';
import { DynamicFieldDirective } from './dynamic-field.directive';
import { DynamicSearchComponent } from './dynamic-search/dynamic-search.component';
import { ConfirmDeleteDialogComponent } from './confirm-delete/confirm-delete.component';
import { FormInputComponent } from './form-input/form-input.component';
import { FormTextareaComponent } from './form-textarea/form-textarea.component';
import { FormSelectComponent } from './form-select/form-select.component';
import { FormButtonComponent } from './form-button/form-button.component';
import { FormAutocompleteComponent } from './form-autocomplete/form-autocomplete.component';
import { FormCheckboxComponent } from './form-checkbox/form-checkbox.component';
import { FormSlideToggleComponent } from './form-slidetoggle/form-slidetoggle.component';
import { ChangelogComponent } from './changelog/changelog.component';

@NgModule( {
    imports: [
        CommonModule, FormsModule, ReactiveFormsModule,
        MatInputModule, MatAutocompleteModule, MatSelectModule, MatButtonModule, MatCheckboxModule,
        MatMenuModule, MatToolbarModule, MatTabsModule, MatIconModule, MatCardModule, MatProgressSpinnerModule,
        MatFormFieldModule, MatDialogModule, MatGridListModule, MatSlideToggleModule, MatPaginatorModule, MatStepperModule, MatTableModule,
        CoreModule, TranslatorModule
    ],
    exports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        MatInputModule, MatAutocompleteModule, MatSelectModule, MatButtonModule, MatCheckboxModule,
        MatMenuModule, MatToolbarModule, MatTabsModule, MatIconModule, MatCardModule, MatProgressSpinnerModule,
        MatFormFieldModule, MatDialogModule, MatSlideToggleModule, MatPaginatorModule, MatStepperModule, MatTableModule,
        FormerrorComponent, DynamicFormComponent, DynamicSearchComponent
    ],
    declarations: [
        FormerrorComponent,
        FormButtonComponent,
        FormInputComponent,
        FormTextareaComponent,
        FormCheckboxComponent,
        FormSlideToggleComponent,
        FormSelectComponent,
        FormAutocompleteComponent,
        DynamicFormComponent,
        ConfirmDeleteDialogComponent,
        DynamicFieldDirective,
        DynamicSearchComponent,
        ChangelogComponent
    ],
    providers: [
        // Avoid providing services in shared modules. Services are usually singletons that are provided once for
        // the entire application or in a particular feature module.
    ],
    // When we want a component to be able to be created dynamically, we need to let
    // Angular know so it can expose the component factories for us
    entryComponents: [
        FormButtonComponent,
        FormInputComponent,
        FormTextareaComponent,
        FormCheckboxComponent,
        FormSlideToggleComponent,
        FormSelectComponent,
        FormAutocompleteComponent,
        ConfirmDeleteDialogComponent,
        ChangelogComponent
    ]
} )
export class SharedModule { }
