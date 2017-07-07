import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ItgmSharedModule } from '../../shared';
import {
    AvaliacaoService,
    AvaliacaoPopupService,
    AvaliacaoComponent,
    AvaliacaoDetailComponent,
    AvaliacaoDialogComponent,
    AvaliacaoPopupComponent,
    AvaliacaoDeletePopupComponent,
    AvaliacaoDeleteDialogComponent,
    avaliacaoRoute,
    avaliacaoPopupRoute,
    AvaliacaoResolvePagingParams,
} from './';

const ENTITY_STATES = [
    ...avaliacaoRoute,
    ...avaliacaoPopupRoute,
];

@NgModule({
    imports: [
        ItgmSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        AvaliacaoComponent,
        AvaliacaoDetailComponent,
        AvaliacaoDialogComponent,
        AvaliacaoDeleteDialogComponent,
        AvaliacaoPopupComponent,
        AvaliacaoDeletePopupComponent,
    ],
    entryComponents: [
        AvaliacaoComponent,
        AvaliacaoDialogComponent,
        AvaliacaoPopupComponent,
        AvaliacaoDeleteDialogComponent,
        AvaliacaoDeletePopupComponent,
    ],
    providers: [
        AvaliacaoService,
        AvaliacaoPopupService,
        AvaliacaoResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ItgmAvaliacaoModule {}
