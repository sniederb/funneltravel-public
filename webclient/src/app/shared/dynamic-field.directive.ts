import { ComponentFactoryResolver, Directive, Input, ComponentRef, OnInit, ViewContainerRef } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { FormButtonComponent } from './form-button/form-button.component';
import { FormInputComponent } from './form-input/form-input.component';
import { FormTextareaComponent } from './form-textarea/form-textarea.component';
import { FormCheckboxComponent } from './form-checkbox/form-checkbox.component';
import { FormSlideToggleComponent } from './form-slidetoggle/form-slidetoggle.component';
import { FormSelectComponent } from './form-select/form-select.component';
import { FormAutocompleteComponent } from './form-autocomplete/form-autocomplete.component';

const components = {
    button: FormButtonComponent,
    text: FormInputComponent,
    largetext: FormTextareaComponent,
    password: FormInputComponent,
    readonly: FormInputComponent,
    'datetime-local': FormInputComponent,
    date: FormInputComponent,
    select: FormSelectComponent,
    checkbox: FormCheckboxComponent,
    slidetoggle: FormSlideToggleComponent,
    autocomplete: FormAutocompleteComponent
};

@Directive( {
    selector: '[appDynamicField]'
} )
export class DynamicFieldDirective implements OnInit {

    @Input()
    config;

    @Input()
    group: FormGroup;
    component: ComponentRef<any>;

    constructor( private resolver: ComponentFactoryResolver,
        private container: ViewContainerRef ) { }

    ngOnInit() {
        const component = components[this.config.formControl];
        const factory = this.resolver.resolveComponentFactory<any>( component );
        this.component = this.container.createComponent( factory );
        this.component.instance.config = this.config;
        this.component.instance.group = this.group;
    }

}
