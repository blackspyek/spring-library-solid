import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment';
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  console.log(environment.apiUrl);
  if (req.url.startsWith(environment.apiUrl)) {
    const modifiedReq = req.clone({
      withCredentials: true,
    });
    return next(modifiedReq);
  }

  return next(req);
};
