import { Component, OnInit, ElementRef } from '@angular/core';
import { TranslatorService } from '../translator.service';

@Component( {
    selector: 'app-codekeys',
    templateUrl: './codekeys.component.html',
    styleUrls: ['./codekeys.component.scss']
} )
export class CodekeysComponent implements OnInit {

    constructor( private translator: TranslatorService, private myElement: ElementRef ) { }

    ngOnInit() {
        // 'forEach' on non-live node lists isn't supported in all browsers ...
        const nonLiveNodeList = this.myElement.nativeElement.querySelectorAll( 'span.codekey_entry' );

        for ( let i = 0; i < nonLiveNodeList.length; i++ ) {
            const codeKeyElement = nonLiveNodeList[i];
            this.translator.add( codeKeyElement.getAttribute( 'codeKey' ), codeKeyElement.innerText );
        }
    }

}
