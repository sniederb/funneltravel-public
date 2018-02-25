import { NgModule } from '@angular/core';
import { CodekeysComponent } from './codekeys/codekeys.component';
import { TranslatorService } from './translator.service';

@NgModule( {
    imports: [],
    exports: [CodekeysComponent],
    declarations: [CodekeysComponent],
    providers: [TranslatorService]
} )
export class TranslatorModule { }
