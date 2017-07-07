import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { AvaliacaoComponent } from './avaliacao.component';
import { AvaliacaoDetailComponent } from './avaliacao-detail.component';
import { AvaliacaoPopupComponent } from './avaliacao-dialog.component';
import { AvaliacaoDeletePopupComponent } from './avaliacao-delete-dialog.component';

import { Principal } from '../../shared';

@Injectable()
export class AvaliacaoResolvePagingParams implements Resolve<any> {

  constructor(private paginationUtil: PaginationUtil) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
      const page = route.queryParams['page'] ? route.queryParams['page'] : '1';
      const sort = route.queryParams['sort'] ? route.queryParams['sort'] : 'id,asc';
      return {
          page: this.paginationUtil.parsePage(page),
          predicate: this.paginationUtil.parsePredicate(sort),
          ascending: this.paginationUtil.parseAscending(sort)
    };
  }
}

export const avaliacaoRoute: Routes = [
  {
    path: 'avaliacao',
    component: AvaliacaoComponent,
    resolve: {
      'pagingParams': AvaliacaoResolvePagingParams
    },
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'itgmApp.avaliacao.home.title'
    },
    canActivate: [UserRouteAccessService]
  }, {
    path: 'avaliacao/:id',
    component: AvaliacaoDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'itgmApp.avaliacao.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const avaliacaoPopupRoute: Routes = [
  {
    path: 'avaliacao-new',
    component: AvaliacaoPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'itgmApp.avaliacao.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'avaliacao/:id/edit',
    component: AvaliacaoPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'itgmApp.avaliacao.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  },
  {
    path: 'avaliacao/:id/delete',
    component: AvaliacaoDeletePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'itgmApp.avaliacao.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
